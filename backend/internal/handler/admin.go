package handler

import (
	"encoding/json"
	"net/http"

	"lovisual-backend/internal/repository"
)

func RegisterAdmin(mux *http.ServeMux, db *repository.SQLite) {
	mux.HandleFunc("GET /health", func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte(`{"status":"ok"}`))
	})

	mux.HandleFunc("GET /stats", func(w http.ResponseWriter, r *http.Request) {
		var count int
		db.DB.QueryRow(`SELECT COUNT(*) FROM users`).Scan(&count)
		json.NewEncoder(w).Encode(map[string]int{"users": count})
	})
}
