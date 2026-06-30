package handler

import (
	"net/http"
	"strings"

	"lovisual-backend/internal/model"
	"lovisual-backend/internal/service"
)

func bearerToken(r *http.Request) string {
	t := r.Header.Get("Authorization")
	if strings.HasPrefix(t, "Bearer ") {
		return t[7:]
	}
	return t
}

func mustAuth(a *service.Auth, r *http.Request) *model.User {
	token := bearerToken(r)
	user, err := a.Validate(token)
	if err != nil {
		panic("unauthorized")
	}
	return user
}

func RecoverAuth(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		defer func() {
			if v := recover(); v != nil {
				if v == "unauthorized" {
					http.Error(w, `{"error":"unauthorized"}`, 401)
				} else {
					panic(v)
				}
			}
		}()
		next.ServeHTTP(w, r)
	})
}
