package middleware

import (
	"log"
	"net/http"
	"sync"
	"time"
)

type Middleware func(http.Handler) http.Handler

func Chain(mws ...Middleware) Middleware {
	return func(next http.Handler) http.Handler {
		for i := len(mws) - 1; i >= 0; i-- {
			next = mws[i](next)
		}
		return next
	}
}

func CORS(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Access-Control-Allow-Methods", "GET,POST,PATCH,DELETE,OPTIONS")
		w.Header().Set("Access-Control-Allow-Headers", "Authorization,Content-Type")
		if r.Method == "OPTIONS" {
			w.WriteHeader(204)
			return
		}
		next.ServeHTTP(w, r)
	})
}

func Log(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		start := time.Now()
		next.ServeHTTP(w, r)
		log.Printf("%s %s %s", r.Method, r.URL.Path, time.Since(start))
	})
}

func RateLimit(max int, window time.Duration) Middleware {
	type entry struct {
		count  int
		resets time.Time
	}
	mu := sync.Mutex{}
	clients := map[string]*entry{}

	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			ip := r.RemoteAddr
			mu.Lock()
			e, ok := clients[ip]
			if !ok || time.Now().After(e.resets) {
				e = &entry{count: 0, resets: time.Now().Add(window)}
				clients[ip] = e
			}
			e.count++
			if e.count > max {
				mu.Unlock()
				http.Error(w, `{"error":"rate limit"}`, 429)
				return
			}
			mu.Unlock()
			next.ServeHTTP(w, r)
		})
	}
}
