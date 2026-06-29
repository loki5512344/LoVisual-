package repository

import (
	"database/sql"
	"os"
	"path/filepath"

	_ "modernc.org/sqlite"
)

type SQLite struct {
	DB   *sql.DB
	path string
}

func NewSQLite(path string) *SQLite {
	os.MkdirAll(filepath.Dir(path), 0755)
	db, err := sql.Open("sqlite", path+"?_pragma=journal_mode(WAL)&_pragma=busy_timeout(5000)")
	if err != nil {
		panic(err)
	}
	db.SetMaxOpenConns(1)
	return &SQLite{DB: db, path: path}
}

func (s *SQLite) Migrate() error {
	migrations := []string{
		`CREATE TABLE IF NOT EXISTS users (
			id TEXT PRIMARY KEY, username TEXT UNIQUE NOT NULL,
			email TEXT UNIQUE NOT NULL, password TEXT NOT NULL,
			created_at DATETIME DEFAULT CURRENT_TIMESTAMP
		)`,
		`CREATE TABLE IF NOT EXISTS sessions (
			token TEXT PRIMARY KEY, user_id TEXT NOT NULL,
			expires_at DATETIME NOT NULL,
			FOREIGN KEY(user_id) REFERENCES users(id)
		)`,
		`CREATE TABLE IF NOT EXISTS profiles (
			user_id TEXT PRIMARY KEY, nickname TEXT,
			avatar_url TEXT DEFAULT '', playtime INTEGER DEFAULT 0,
			kills INTEGER DEFAULT 0, deaths INTEGER DEFAULT 0,
			level INTEGER DEFAULT 1, xp INTEGER DEFAULT 0,
			FOREIGN KEY(user_id) REFERENCES users(id)
		)`,
		`CREATE TABLE IF NOT EXISTS configs (
			id TEXT PRIMARY KEY, user_id TEXT NOT NULL,
			name TEXT NOT NULL, data TEXT NOT NULL,
			version INTEGER DEFAULT 1, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
			FOREIGN KEY(user_id) REFERENCES users(id)
		)`,
	}
	for _, m := range migrations {
		if _, err := s.DB.Exec(m); err != nil {
			return err
		}
	}
	return nil
}
