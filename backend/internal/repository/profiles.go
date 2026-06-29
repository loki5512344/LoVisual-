package repository

import "lovisual-backend/internal/model"

func (s *SQLite) CreateProfile(p *model.Profile) error {
	_, err := s.DB.Exec(
		`INSERT INTO profiles (user_id, nickname, avatar_url) VALUES (?, ?, ?)`,
		p.UserID, p.Nickname, p.AvatarURL,
	)
	return err
}

func (s *SQLite) GetProfile(userID string) (*model.Profile, error) {
	p := &model.Profile{}
	err := s.DB.QueryRow(
		`SELECT user_id, nickname, avatar_url, playtime, kills, deaths, level, xp
		 FROM profiles WHERE user_id = ?`, userID,
	).Scan(&p.UserID, &p.Nickname, &p.AvatarURL, &p.Playtime, &p.Kills, &p.Deaths, &p.Level, &p.XP)
	if err != nil {
		return nil, err
	}
	return p, nil
}

func (s *SQLite) UpdateProfile(p *model.Profile) error {
	_, err := s.DB.Exec(
		`UPDATE profiles SET nickname=?, avatar_url=?, playtime=?, kills=?, deaths=?, level=?, xp=?
		 WHERE user_id=?`,
		p.Nickname, p.AvatarURL, p.Playtime, p.Kills, p.Deaths, p.Level, p.XP, p.UserID,
	)
	return err
}
