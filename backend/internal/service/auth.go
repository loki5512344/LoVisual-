package service

import (
	"crypto/rand"
	"crypto/sha256"
	"encoding/hex"
	"errors"
	"fmt"
	"time"

	"lovisual-backend/internal/model"
	"lovisual-backend/internal/repository"
)

type Auth struct {
	db     *repository.SQLite
	secret string
}

func NewAuth(db *repository.SQLite, secret string) *Auth {
	return &Auth{db: db, secret: secret}
}

func (a *Auth) Register(email, username, password string) (*model.User, error) {
	hash := fmt.Sprintf("%x", sha256.Sum256([]byte(password+a.secret)))
	id := newID()
	u := &model.User{ID: id, Username: username, Email: email, Password: hash}
	if err := a.db.CreateUser(u); err != nil {
		return nil, errors.New("user exists")
	}
	a.db.CreateProfile(&model.Profile{UserID: id, Nickname: username})
	return u, nil
}

func (a *Auth) Login(email, password string) (*model.User, string, error) {
	u, err := a.db.GetUserByEmail(email)
	if err != nil {
		return nil, "", errors.New("invalid credentials")
	}
	hash := fmt.Sprintf("%x", sha256.Sum256([]byte(password+a.secret)))
	if u.Password != hash {
		return nil, "", errors.New("invalid credentials")
	}
	token := newID() + newID()
	ses := &model.Session{Token: token, UserID: u.ID, ExpiresAt: time.Now().Add(7 * 24 * time.Hour)}
	a.db.CreateSession(ses)
	return u, token, nil
}

func (a *Auth) Validate(token string) (*model.User, error) {
	ses, err := a.db.GetSession(token)
	if err != nil {
		return nil, errors.New("invalid session")
	}
	if time.Now().After(ses.ExpiresAt) {
		a.db.DeleteSession(token)
		return nil, errors.New("session expired")
	}
	return a.db.GetUserByID(ses.UserID)
}

func (a *Auth) Logout(token string) {
	a.db.DeleteSession(token)
}

func newID() string {
	b := make([]byte, 16)
	rand.Read(b)
	return hex.EncodeToString(b)
}
