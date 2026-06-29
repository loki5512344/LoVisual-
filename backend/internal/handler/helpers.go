package handler

import (
	"net/http"

	"lovisual-backend/internal/model"
	"lovisual-backend/internal/service"
)

func mustAuth(a *service.Auth, r *http.Request) *model.User {
	token := r.Header.Get("Authorization")
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
				http.Error(w, `{"error":"unauthorized"}`, 401)
			}
		}()
		next.ServeHTTP(w, r)
	})
}
