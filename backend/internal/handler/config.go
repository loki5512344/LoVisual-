package handler

import (
	"encoding/json"
	"net/http"

	"lovisual-backend/internal/model"
	"lovisual-backend/internal/repository"
)

func RegisterConfig(mux *http.ServeMux, db *repository.SQLite, _ interface{}) {
	mux.HandleFunc("POST /configs", func(w http.ResponseWriter, r *http.Request) {
		var b struct {
			UserID string `json:"user_id"`
			Name   string `json:"name"`
			Data   string `json:"data"`
		}
		if err := json.NewDecoder(r.Body).Decode(&b); err != nil {
			http.Error(w, `{"error":"bad request"}`, 400)
			return
		}
		if b.UserID == "" || b.Name == "" {
			http.Error(w, `{"error":"user_id and name required"}`, 400)
			return
		}
		c := &model.CloudConfig{
			ID:     b.UserID + "-" + b.Name,
			UserID: b.UserID,
			Name:   b.Name,
			Data:   b.Data,
		}
		if err := db.SaveConfig(c); err != nil {
			http.Error(w, `{"error":"`+err.Error()+`"}`, 409)
			return
		}
		json.NewEncoder(w).Encode(map[string]string{
			"status":    "saved",
			"share_key": c.ShareKey,
		})
	})

	mux.HandleFunc("GET /configs/share/{key}", func(w http.ResponseWriter, r *http.Request) {
		key := r.PathValue("key")
		c, err := db.GetConfigByShareKey(key)
		if err != nil {
			http.Error(w, `{"error":"not found"}`, 404)
			return
		}
		json.NewEncoder(w).Encode(c)
	})

	mux.HandleFunc("GET /configs/user/{id}", func(w http.ResponseWriter, r *http.Request) {
		configs, _ := db.GetConfigs(r.PathValue("id"))
		json.NewEncoder(w).Encode(configs)
	})

	mux.HandleFunc("DELETE /configs/{user}/{name}", func(w http.ResponseWriter, r *http.Request) {
		db.DeleteConfig(r.PathValue("user") + "-" + r.PathValue("name"))
		w.Write([]byte(`{"status":"deleted"}`))
	})
}
