package service

import (
	"lovisual-backend/internal/model"
	"lovisual-backend/internal/repository"
)

type Profile struct {
	db *repository.SQLite
}

func NewProfile(db *repository.SQLite) *Profile {
	return &Profile{db: db}
}

func (s *Profile) Get(userID string) (*model.Profile, error) {
	return s.db.GetProfile(userID)
}

func (s *Profile) Update(p *model.Profile) error {
	return s.db.UpdateProfile(p)
}

func (s *Profile) AddPlaytime(userID string, seconds int64) error {
	p, err := s.db.GetProfile(userID)
	if err != nil {
		return err
	}
	p.Playtime += seconds
	p.XP += int(seconds)
	p.Level = p.XP / 3600
	if p.Level < 1 {
		p.Level = 1
	}
	return s.db.UpdateProfile(p)
}

func (s *Profile) GetByUsername(username string) (*model.User, error) {
	return s.db.GetUserByUsername(username)
}

func (s *Profile) SearchUsers(query string) ([]model.User, error) {
	return s.db.SearchUsers(query)
}

func (s *Profile) AddFriend(userID, friendID string) error {
	return s.db.AddFriend(userID, friendID)
}

func (s *Profile) RemoveFriend(userID, friendID string) error {
	return s.db.RemoveFriend(userID, friendID)
}

func (s *Profile) GetFriends(userID string) ([]string, error) {
	return s.db.GetFriends(userID)
}
