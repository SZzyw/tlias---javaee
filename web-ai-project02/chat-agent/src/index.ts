import "dotenv/config";
import { serve } from "@hono/node-server";
import { Hono } from "hono";
import { cors } from "hono/cors";
import { runChatAgent } from "./agent";

const app = new Hono();
const port = Number(process.env.PORT || 8787);

app.use("/*", cors());

app.post("/api/chat", async (c) => {
  const body = (await c.req.json()) as { messages?: any[] };
  const messages = body.messages ?? [];

  try {
    const result = await runChatAgent(messages);
    return result.toUIMessageStreamResponse({ sendReasoning: true });
  } catch (error) {
    const message = error instanceof Error ? error.message : "Chat agent error";
    return c.json({ error: message }, 500);
  }
});

serve({ fetch: app.fetch, port }, (info) => {
  console.log(`Chat agent listening on http://localhost:${info.port}`);
  console.log(`POST /api/chat`);
});
