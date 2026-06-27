# 🧠 WPM Digital Infrastructure

> Self-hosted AI stack for business intelligence, automation, and productivity — running locally in Barranquilla, Colombia.

## Stack

| Service | Purpose | Port |
|---------|---------|------|
| **Odysseus** | AI Workspace (chat, RAG, agents) | `7000` |
| **n8n** | Workflow automation | `5678` |
| **Ollama** | Local LLM inference (runs on host) | `11434` |
| **ChromaDB** | Vector database for embeddings | `8100` |
| **SearXNG** | Private meta-search engine | `8080` |
| **ntfy** | Push notifications | `8091` |

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                  WPM Infrastructure                  │
│                                                     │
│  ┌──────────┐    ┌──────────┐    ┌──────────────┐  │
│  │ Odysseus │───▶│ ChromaDB │    │    SearXNG   │  │
│  │ :7000    │    │  :8100   │    │    :8080     │  │
│  └────┬─────┘    └──────────┘    └──────────────┘  │
│       │                                  ▲          │
│       │ queries                          │ search   │
│       ▼                                  │          │
│  ┌──────────┐                   ┌────────┴─────┐   │
│  │  Ollama  │                   │     n8n      │   │
│  │  :11434  │                   │    :5678     │   │
│  │ (host)   │                   │ automations  │   │
│  └──────────┘                   └──────────────┘   │
│                                                     │
│  ┌──────────┐                                       │
│  │   ntfy   │  push notifications                   │
│  │  :8091   │                                       │
│  └──────────┘                                       │
└─────────────────────────────────────────────────────┘
```

## Quick Start

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) + Docker Compose
- [Ollama](https://ollama.com/) installed on the host machine
- At least 8GB RAM, 20GB disk

### 1. Clone

```bash
git clone https://github.com/wilmerjoseperezorozco-dev/wpm-digital-infrastructure.git
cd wpm-digital-infrastructure
```

### 2. Configure environment

```bash
cp .env.example .env
# Edit .env with your values
```

### 3. Pull a local model (on host)

```bash
ollama pull llama3.2
```

### 4. Launch

```bash
docker compose up -d --build
```

### 5. Access

| Service | URL |
|---------|-----|
| Odysseus AI | http://localhost:7000 |
| n8n Automation | http://localhost:5678 |
| SearXNG | http://localhost:8080 |
| ChromaDB | http://localhost:8100 |
| ntfy | http://localhost:8091 |

## Data Persistence

All data is stored in named Docker volumes:

```
odysseus-data    → AI workspace data and SQLite DB
odysseus-logs    → Application logs
n8n-data         → Workflow definitions and credentials
chromadb-data    → Vector embeddings
searxng-data     → SearXNG configuration
ntfy-cache       → Notification cache
```

## Stop & Reset

```bash
# Stop services
docker compose down

# Stop and remove all data (destructive)
docker compose down -v
```

## Use Cases

- **Business Intelligence** — Query local documents with RAG via Odysseus
- **Workflow Automation** — Connect APIs and automate tasks with n8n
- **Private Search** — Search the web without tracking via SearXNG
- **AI Agents** — Run local LLMs through Ollama for offline inference

## Contributing

PRs welcome. Open an issue first to discuss major changes.

## License

MIT
