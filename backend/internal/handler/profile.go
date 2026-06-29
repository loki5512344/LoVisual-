package handler

import (
	"encoding/json"
	"net/http"

	"lovisual-backend/internal/service"
)

func RegisterProfile(mux *http.ServeMux, p *service.Profile, a *service.Auth) {
	mux.HandleFunc("GET /profile", func(w http.ResponseWriter, r *http.Request) {
		user := mustAuth(a, r)
		prof, err := p.Get(user.ID)
		if err != nil {
			http.Error(w, `{"error":"not found"}`, 404)
			return
		}
		json.NewEncoder(w).Encode(prof)
	})

	mux.HandleFunc("PATCH /profile", func(w http.ResponseWriter, r *http.Request) {
		user := mustAuth(a, r)
		var updates struct {
			Nickname  string `json:"nickname"`
			AvatarURL string `json:"avatar_url"`
		}
		json.NewDecoder(r.Body).Decode(&updates)
		prof, _ := p.Get(user.ID)
		if updates.Nickname != "" {
			prof.Nickname = updates.Nickname
		}
		if updates.AvatarURL != "" {
			prof.AvatarURL = updates.AvatarURL
		}
		p.Update(prof)
		json.NewEncoder(w).Encode(prof)
	})

	mux.HandleFunc("POST /profile/playtime", func(w http.ResponseWriter, r *http.Request) {
		user := mustAuth(a, r)
		var b struct{ Seconds int64 `json:"seconds"` }
		json.NewDecoder(r.Body).Decode(&b)
		p.AddPlaytime(user.ID, b.Seconds)
		w.Write([]byte(`{"status":"ok"}`))
	})
}
