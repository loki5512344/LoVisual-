package repository

import (
	"lovisual-backend/internal/model"
)

func (s *SQLite) CreateUser(u *model.User) error {
	_, err := s.DB.Exec(
		`INSERT INTO users (id, username, email, password) VALUES (?, ?, ?, ?)`,
		u.ID, u.Username, u.Email, u.Password,
	)
	return err
}

func (s *SQLite) GetUserByEmail(email string) (*model.User, error) {
	u := &model.User{}
	err := s.DB.QueryRow(
		`SELECT id, username, email, password, created_at FROM users WHERE email = ?`, email,
	).Scan(&u.ID, &u.Username, &u.Email, &u.Password, &u.CreatedAt)
	if err != nil {
		return nil, err
	}
	return u, nil
}

func (s *SQLite) GetUserByID(id string) (*model.User, error) {
	u := &model.User{}
	err := s.DB.QueryRow(
		`SELECT id, username, email, password, created_at FROM users WHERE id = ?`, id,
	).Scan(&u.ID, &u.Username, &u.Email, &u.Password, &u.CreatedAt)
	if err != nil {
		return nil, err
	}
	return u, nil
}
