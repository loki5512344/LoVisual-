use serde::{Deserialize, Serialize};

#[derive(Serialize)]
pub struct LoginRequest {
    pub email: String,
    pub password: String,
}

#[derive(Serialize)]
pub struct RegisterRequest {
    pub email: String,
    pub username: String,
    pub password: String,
}

#[derive(Deserialize)]
pub struct AuthResponse {
    pub token: Option<String>,
    pub username: Option<String>,
    pub error: Option<String>,
}

#[derive(Deserialize)]
pub struct Profile {
    pub user_id: String,
    pub nickname: String,
    pub avatar_url: String,
    pub playtime: i64,
}

pub struct Client {
    base: String,
    token: Option<String>,
}

impl Client {
    pub fn new() -> Self {
        Self {
            base: "http://localhost:8080".into(),
            token: None,
        }
    }

    pub fn login(&mut self, email: &str, password: &str) -> Result<(String, String), String> {
        let body = LoginRequest {
            email: email.into(),
            password: password.into(),
        };
        let resp: AuthResponse = reqwest::blocking::Client::new()
            .post(format!("{}/auth/login", self.base))
            .json(&body)
            .send()
            .map_err(|e| e.to_string())?
            .json()
            .map_err(|e| e.to_string())?;

        match (resp.token, resp.error, resp.username) {
            (Some(t), _, Some(u)) => {
                self.token = Some(t.clone());
                Ok((t, u))
            }
            (_, Some(e), _) => Err(e),
            (None, None, _) => Err("unknown error".into()),
            _ => Err("missing username".into()),
        }
    }

    pub fn register(&mut self, email: &str, user: &str, pass: &str) -> Result<String, String> {
        let body = RegisterRequest {
            email: email.into(),
            username: user.into(),
            password: pass.into(),
        };
        let resp: AuthResponse = reqwest::blocking::Client::new()
            .post(format!("{}/auth/register", self.base))
            .json(&body)
            .send()
            .map_err(|e| e.to_string())?
            .json()
            .map_err(|e| e.to_string())?;

        match (resp.error, resp.username) {
            (Some(e), _) => Err(e),
            (_, Some(u)) => Ok(u),
            (None, None) => Err("unknown error".into()),
        }
    }

    pub fn get_profile(&self) -> Result<Profile, String> {
        let token = self.token.as_ref().ok_or("not logged in")?;
        reqwest::blocking::Client::new()
            .get(format!("{}/profile", self.base))
            .header("Authorization", token)
            .send()
            .map_err(|e| e.to_string())?
            .json()
            .map_err(|e| e.to_string())
    }
}
