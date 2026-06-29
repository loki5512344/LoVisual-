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

	mux.HandleFunc("GET /profile/search", func(w http.ResponseWriter, r *http.Request) {
		q := r.URL.Query().Get("q")
		if q == "" {
			http.Error(w, `{"error":"missing query"}`, 400)
			return
		}
		users, err := p.SearchUsers(q)
		if err != nil {
			http.Error(w, `{"error":"search failed"}`, 500)
			return
		}
		json.NewEncoder(w).Encode(users)
	})

	mux.HandleFunc("POST /profile/friends/{id}", func(w http.ResponseWriter, r *http.Request) {
		user := mustAuth(a, r)
		friendID := r.PathValue("id")
		if err := p.AddFriend(user.ID, friendID); err != nil {
			http.Error(w, `{"error":"add friend failed"}`, 500)
			return
		}
		w.Write([]byte(`{"status":"ok"}`))
	})

	mux.HandleFunc("DELETE /profile/friends/{id}", func(w http.ResponseWriter, r *http.Request) {
		user := mustAuth(a, r)
		friendID := r.PathValue("id")
		if err := p.RemoveFriend(user.ID, friendID); err != nil {
			http.Error(w, `{"error":"remove friend failed"}`, 500)
			return
		}
		w.Write([]byte(`{"status":"ok"}`))
	})

	mux.HandleFunc("GET /profile/friends", func(w http.ResponseWriter, r *http.Request) {
		user := mustAuth(a, r)
		friends, err := p.GetFriends(user.ID)
		if err != nil {
			http.Error(w, `{"error":"list friends failed"}`, 500)
			return
		}
		json.NewEncoder(w).Encode(struct {
			Friends []string `json:"friends"`
		}{friends})
	})
}
