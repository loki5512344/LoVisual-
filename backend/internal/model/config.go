package model

import "time"

type CloudConfig struct {
	ID        string    `json:"id"`
	UserID    string    `json:"user_id"`
	Name      string    `json:"name"`
	Data      string    `json:"data"` // raw .loconf content
	Version   int       `json:"version"`
	UpdatedAt time.Time `json:"updated_at"`
}

type SyncEvent struct {
	Type    string `json:"type"` // "config_update", "profile_update"
	UserID  string `json:"user_id"`
	Payload string `json:"payload"`
}
