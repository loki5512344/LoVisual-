package handler

import (
	"encoding/json"
	"net/http"
)

type VersionResponse struct {
	Version     string `json:"version"`
	ModURL      string `json:"mod_url"`
	LauncherURL string `json:"launcher_url"`
	Changelog   string `json:"changelog"`
}

func RegisterVersion(mux *http.ServeMux) {
	mux.HandleFunc("GET /version", getVersion)
}

func getVersion(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(VersionResponse{
		Version:     "1.0.0",
		ModURL:      "https://github.com/loki5512344/LoVisual-/releases/latest/download/lovisual-0.1.0.jar",
		LauncherURL: "",
		Changelog:   "Initial release",
	})
}
