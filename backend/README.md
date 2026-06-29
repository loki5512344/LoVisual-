# LoVisual Backend

Go HTTP API server for LoVisual client.

## Run

```bash
go run ./cmd/server
```

Default: `:8080`

## API

| Method | Path | Description |
|--------|------|-------------|
| POST | `/auth/register` | Register (email, username, password) |
| POST | `/auth/login` | Login, returns token |
| POST | `/auth/logout` | Invalidate session |
| GET | `/profile` | Get own profile |
| PATCH | `/profile` | Update nickname/avatar |
| POST | `/profile/playtime` | Sync playtime |
| GET | `/profile/search?q=` | Search users |
| POST | `/profile/friends/{id}` | Add friend |
| DELETE | `/profile/friends/{id}` | Remove friend |
| GET | `/profile/friends` | List friends |
| POST | `/configs` | Upload config |
| GET | `/configs/share/{key}` | Download by share key |
| GET | `/configs/user/{id}` | List user configs |
| DELETE | `/configs/{user}/{name}` | Delete config |
| WS | `/ws` | IRC WebSocket |

## Stack

- Go + chi router
- SQLite
- Rate limiting, CORS
- SHA256 password hashing
