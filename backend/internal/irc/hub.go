package irc

import (
	"log"
	"net/http"
	"sync"

	"golang.org/x/net/websocket"
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
		var msg []byte
		if err := websocket.Message.Receive(c.conn, &msg); err != nil {
			break
		}
		h.mu.RLock()
		for _, client := range h.clients {
			if client.ID != c.ID {
				select {
				case client.send <- msg:
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
