package irc

import (
	"encoding/json"
	"log"
	"net/http"
	"sync"

	"golang.org/x/net/websocket"

	"lovisual-backend/internal/model"
)

type Client struct {
	ID   string
	conn *websocket.Conn
	send chan []byte
}

type Hub struct {
	mu      sync.RWMutex
	clients map[string]*Client
}

func NewHub() *Hub {
	return &Hub{clients: make(map[string]*Client)}
}

func (h *Hub) ServeWS(w http.ResponseWriter, r *http.Request) {
	websocket.Handler(func(conn *websocket.Conn) {
		id := r.RemoteAddr
		c := &Client{ID: id, conn: conn, send: make(chan []byte, 64)}
		h.mu.Lock()
		h.clients[id] = c
		h.mu.Unlock()

		log.Printf("irc: %s connected", id)

		go h.writeLoop(c)
		h.readLoop(c)

		h.mu.Lock()
		delete(h.clients, id)
		h.mu.Unlock()
		close(c.send)
		log.Printf("irc: %s disconnected", id)
	}).ServeHTTP(w, r)
}

func (h *Hub) readLoop(c *Client) {
	for {
		var raw []byte
		if err := websocket.Message.Receive(c.conn, &raw); err != nil {
			break
		}

		var wsMsg model.WSMessage
		if err := json.Unmarshal(raw, &wsMsg); err != nil {
			log.Printf("irc: invalid message from %s: %v", c.ID, err)
			continue
		}

		if wsMsg.Type != "irc_message" {
			continue
		}

		var ircMsg model.IRCMessage
		if err := json.Unmarshal(wsMsg.Payload, &ircMsg); err != nil {
			log.Printf("irc: invalid IRC payload from %s: %v", c.ID, err)
			continue
		}

		ircMsg.From = c.ID

		payload, err := json.Marshal(ircMsg)
		if err != nil {
			log.Printf("irc: marshal error: %v", err)
			continue
		}
		wsMsg.Payload = payload

		out, err := json.Marshal(wsMsg)
		if err != nil {
			log.Printf("irc: marshal error: %v", err)
			continue
		}

		h.mu.RLock()
		for _, client := range h.clients {
			if client.ID != c.ID {
				select {
				case client.send <- out:
				default:
				}
			}
		}
		h.mu.RUnlock()
	}
}

func (h *Hub) writeLoop(c *Client) {
	for msg := range c.send {
		if err := websocket.Message.Send(c.conn, msg); err != nil {
			break
		}
	}
}

func (h *Hub) Close() {
	h.mu.Lock()
	defer h.mu.Unlock()
	for _, c := range h.clients {
		c.conn.Close()
	}
}
