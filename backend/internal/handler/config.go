package handler

import (
	"encoding/json"
	"net/http"

	"lovisual-backend/internal/model"
	"lovisual-backend/internal/repository"
	"lovisual-backend/internal/service"
)

func RegisterConfig(mux *http.ServeMux, db *repository.SQLite, a *service.Auth) {
	mux.HandleFunc("GET /configs", func(w http.ResponseWriter, r *http.Request) {
		user := mustAuth(a, r)
		configs, _ := db.GetConfigs(user.ID)
		json.NewEncoder(w).Encode(configs)
	})

	mux.HandleFunc("POST /configs", func(w http.ResponseWriter, r *http.Request) {
		user := mustAuth(a, r)
		var b struct {
			Name string `json:"name"`
			Data string `json:"data"`
		}
		json.NewDecoder(r.Body).Decode(&b)
		c := &model.CloudConfig{
			ID:     user.ID + "-" + b.Name,
			UserID: user.ID,
			Name:   b.Name,
			Data:   b.Data,
		}
		db.SaveConfig(c)
		w.Write([]byte(`{"status":"saved"}`))
	})

	mux.HandleFunc("DELETE /configs/{name}", func(w http.ResponseWriter, r *http.Request) {
		user := mustAuth(a, r)
		id := user.ID + "-" + r.PathValue("name")
		db.DeleteConfig(id)
		w.Write([]byte(`{"status":"deleted"}`))
	})
}
