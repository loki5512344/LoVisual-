package repository

import (
	"crypto/rand"
	"encoding/hex"
	"errors"

	"lovisual-backend/internal/model"
)

func (s *SQLite) genKey() string {
	b := make([]byte, 4)
	rand.Read(b)
	return hex.EncodeToString(b)
}

func (s *SQLite) SaveConfig(c *model.CloudConfig) error {
	// limit: max 5 configs per user
	var count int
	s.DB.QueryRow(`SELECT COUNT(*) FROM configs WHERE user_id = ?`, c.UserID).Scan(&count)
	if count >= 5 {
		return errors.New("max 5 configs per user")
	}

	if c.ShareKey == "" {
		c.ShareKey = s.genKey()
	}

	_, err := s.DB.Exec(
		`INSERT INTO configs (id, user_id, name, data, version, share_key, updated_at)
		 VALUES (?, ?, ?, ?, 1, ?, CURRENT_TIMESTAMP)
		 ON CONFLICT(id) DO UPDATE SET data=?, version=version+1, updated_at=CURRENT_TIMESTAMP`,
		c.ID, c.UserID, c.Name, c.Data, c.ShareKey, c.Data,
	)
	return err
}

func (s *SQLite) GetConfigs(userID string) ([]model.CloudConfig, error) {
	rows, err := s.DB.Query(
		`SELECT id, user_id, name, data, version, share_key, updated_at
		 FROM configs WHERE user_id = ? ORDER BY updated_at DESC`, userID,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var out []model.CloudConfig
	for rows.Next() {
		var c model.CloudConfig
		if err := rows.Scan(&c.ID, &c.UserID, &c.Name, &c.Data, &c.Version, &c.ShareKey, &c.UpdatedAt); err != nil {
			return nil, err
		}
		out = append(out, c)
	}
	return out, nil
}

func (s *SQLite) GetConfigByShareKey(key string) (*model.CloudConfig, error) {
	c := &model.CloudConfig{}
	err := s.DB.QueryRow(
		`SELECT id, user_id, name, data, version, share_key, updated_at
		 FROM configs WHERE share_key = ?`, key,
	).Scan(&c.ID, &c.UserID, &c.Name, &c.Data, &c.Version, &c.ShareKey, &c.UpdatedAt)
	if err != nil {
		return nil, errors.New("config not found")
	}
	return c, nil
}

func (s *SQLite) DeleteConfig(id string) error {
	_, err := s.DB.Exec(`DELETE FROM configs WHERE id = ?`, id)
	return err
}
