package repository

import (
	"crypto/rand"
	"encoding/hex"

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
		`SELECT id, username, email, password, avatar_url, created_at FROM users WHERE email = ?`, email,
	).Scan(&u.ID, &u.Username, &u.Email, &u.Password, &u.AvatarURL, &u.CreatedAt)
	if err != nil {
		return nil, err
	}
	return u, nil
}

func (s *SQLite) GetUserByID(id string) (*model.User, error) {
	u := &model.User{}
	err := s.DB.QueryRow(
		`SELECT id, username, email, password, avatar_url, created_at FROM users WHERE id = ?`, id,
	).Scan(&u.ID, &u.Username, &u.Email, &u.Password, &u.AvatarURL, &u.CreatedAt)
	if err != nil {
		return nil, err
	}
	return u, nil
}

func (s *SQLite) GetUserByUsername(username string) (*model.User, error) {
	u := &model.User{}
	err := s.DB.QueryRow(
		`SELECT id, username, email, password, avatar_url, created_at FROM users WHERE username = ?`, username,
	).Scan(&u.ID, &u.Username, &u.Email, &u.Password, &u.AvatarURL, &u.CreatedAt)
	if err != nil {
		return nil, err
	}
	return u, nil
}

func (s *SQLite) SearchUsers(query string) ([]model.User, error) {
	rows, err := s.DB.Query(
		`SELECT id, username, avatar_url FROM users WHERE username LIKE ? LIMIT 20`, "%"+query+"%",
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var users []model.User
	for rows.Next() {
		var u model.User
		if err := rows.Scan(&u.ID, &u.Username, &u.AvatarURL); err != nil {
			return nil, err
		}
		users = append(users, u)
	}
	return users, nil
}

func (s *SQLite) AddFriend(userID, friendID string) error {
	b := make([]byte, 16)
	rand.Read(b)
	id := hex.EncodeToString(b)
	_, err := s.DB.Exec(
		`INSERT INTO friends (id, user_id, friend_id) VALUES (?, ?, ?)`,
		id, userID, friendID,
	)
	return err
}

func (s *SQLite) RemoveFriend(userID, friendID string) error {
	_, err := s.DB.Exec(
		`DELETE FROM friends WHERE user_id = ? AND friend_id = ?`, userID, friendID,
	)
	return err
}

func (s *SQLite) GetFriends(userID string) ([]string, error) {
	rows, err := s.DB.Query(
		`SELECT u.username FROM friends f JOIN users u ON f.friend_id = u.id WHERE f.user_id = ?`, userID,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var friends []string
	for rows.Next() {
		var name string
		if err := rows.Scan(&name); err != nil {
			return nil, err
		}
		friends = append(friends, name)
	}
	return friends, nil
}
