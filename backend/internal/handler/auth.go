package handler

import (
	"encoding/json"
	"net/http"

	"lovisual-backend/internal/service"
)

type authBody struct {
	Email    string `json:"email"`
	Password string `json:"password"`
	Username string `json:"username,omitempty"`
}

func RegisterAuth(mux *http.ServeMux, a *service.Auth) {
	mux.HandleFunc("POST /auth/register", func(w http.ResponseWriter, r *http.Request) {
		var b authBody
		if err := json.NewDecoder(r.Body).Decode(&b); err != nil {
			http.Error(w, `{"error":"bad request"}`, 400)
			return
		}
		u, err := a.Register(b.Email, b.Username, b.Password)
		if err != nil {
			http.Error(w, `{"error":"`+err.Error()+`"}`, 409)
			return
		}
		json.NewEncoder(w).Encode(map[string]string{"id": u.ID, "username": u.Username})
	})

	mux.HandleFunc("POST /auth/login", func(w http.ResponseWriter, r *http.Request) {
		var b authBody
		if err := json.NewDecoder(r.Body).Decode(&b); err != nil {
			http.Error(w, `{"error":"bad request"}`, 400)
			return
		}
		u, token, err := a.Login(b.Email, b.Password)
		if err != nil {
			http.Error(w, `{"error":"`+err.Error()+`"}`, 401)
			return
		}
		json.NewEncoder(w).Encode(map[string]string{"token": token, "username": u.Username})
	})

	mux.HandleFunc("POST /auth/logout", func(w http.ResponseWriter, r *http.Request) {
		token := bearerToken(r)
		a.Logout(token)
		w.Write([]byte(`{"status":"ok"}`))
	})
}
