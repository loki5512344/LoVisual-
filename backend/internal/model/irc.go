package model

import (
	"encoding/json"
	"time"
)

type IRCMessage struct {
	ID        string    `json:"id"`
	From      string    `json:"from"`
	To        string    `json:"to"` // "" = broadcast
	Content   string    `json:"content"`
	CreatedAt time.Time `json:"created_at"`
}

type WSMessage struct {
	Type    string          `json:"type"`
	Payload json.RawMessage `json:"payload"`
}
