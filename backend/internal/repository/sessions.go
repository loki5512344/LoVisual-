package repository

import "lovisual-backend/internal/model"

func (s *SQLite) CreateSession(ses *model.Session) error {
	_, err := s.DB.Exec(
		`INSERT INTO sessions (token, user_id, expires_at) VALUES (?, ?, ?)`,
		ses.Token, ses.UserID, ses.ExpiresAt,
	)
	return err
}

func (s *SQLite) GetSession(token string) (*model.Session, error) {
	ses := &model.Session{}
	err := s.DB.QueryRow(
		`SELECT token, user_id, expires_at FROM sessions WHERE token = ?`, token,
	).Scan(&ses.Token, &ses.UserID, &ses.ExpiresAt)
	if err != nil {
		return nil, err
	}
	return ses, nil
}

func (s *SQLite) DeleteSession(token string) error {
	_, err := s.DB.Exec(`DELETE FROM sessions WHERE token = ?`, token)
	return err
}
