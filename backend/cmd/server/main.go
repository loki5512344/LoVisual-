package main

import (
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"lovisual-backend/internal/config"
	"lovisual-backend/internal/handler"
	"lovisual-backend/internal/irc"
	"lovisual-backend/internal/middleware"
	"lovisual-backend/internal/repository"
	"lovisual-backend/internal/service"
)

func main() {
	cfg := config.Load()

	db := repository.NewSQLite(cfg.DB.Path)
	if err := db.Migrate(); err != nil {
		log.Fatal("migration:", err)
	}

	hub := irc.NewHub()
	auth := service.NewAuth(db, cfg.JWT.Secret)
	srv := service.NewProfile(db)

	mux := http.NewServeMux()
	handler.RegisterAuth(mux, auth)
	handler.RegisterProfile(mux, srv, auth)
	handler.RegisterConfig(mux, db, nil)
	handler.RegisterAdmin(mux, db)
	handler.RegisterVersion(mux)

	mux.HandleFunc("GET /ws", hub.ServeWS)

	stack := middleware.Chain(
		handler.RecoverAuth,
		middleware.CORS,
		middleware.RateLimit(100, time.Minute),
		middleware.Log,
	)

	server := &http.Server{
		Addr:         ":" + cfg.Port,
		Handler:      stack(mux),
		ReadTimeout:  10 * time.Second,
		WriteTimeout: 10 * time.Second,
		IdleTimeout:  60 * time.Second,
	}

	go func() {
		log.Printf("Listening on :%s", cfg.Port)
		if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatal(err)
		}
	}()

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit
	log.Println("Shutting down...")
	hub.Close()
}
