package repository

import "lovisual-backend/internal/model"

func (s *SQLite) SaveConfig(c *model.CloudConfig) error {
	_, err := s.DB.Exec(
		`INSERT INTO configs (id, user_id, name, data, version, updated_at)
		 VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
		 ON CONFLICT(id) DO UPDATE SET data=?, version=version+1, updated_at=CURRENT_TIMESTAMP`,
		c.ID, c.UserID, c.Name, c.Data, c.Version, c.Data,
	)
	return err
}

func (s *SQLite) GetConfigs(userID string) ([]model.CloudConfig, error) {
	rows, err := s.DB.Query(
		`SELECT id, user_id, name, data, version, updated_at
		 FROM configs WHERE user_id = ? ORDER BY updated_at DESC`, userID,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var out []model.CloudConfig
	for rows.Next() {
		var c model.CloudConfig
		if err := rows.Scan(&c.ID, &c.UserID, &c.Name, &c.Data, &c.Version, &c.UpdatedAt); err != nil {
			return nil, err
		}
		out = append(out, c)
	}
	return out, nil
}

func (s *SQLite) DeleteConfig(id string) error {
	_, err := s.DB.Exec(`DELETE FROM configs WHERE id = ?`, id)
	return err
}
