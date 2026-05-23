import {
  convertWidgetMessagesToModelMessages,
  createOpenAI,
  stepCountIs,
  streamText,
  type UIMessage,
} from "../../../openchatwidget/sdk/widget/src/index";

export async function runChatAgent(messages: UIMessage[]) {
  const modelId = process.env.OPENAI_MODEL || "gpt-5-mini";
  const baseSystemPrompt =
    process.env.CHAT_AGENT_PROMPT ||
    "你是教育管理系统的智能助手。请用中文回复。" +
    "你可以回答关于系统功能的问题，也可以帮助用户了解教育管理相关的知识。" +
    "保持回答简洁、有用。";

  const openai = createOpenAI({
    apiKey: process.env.OPENAI_API_KEY,
    baseURL: process.env.OPENAI_BASE_URL,
  });

  return streamText({
    model: openai.chat(modelId),
    system: baseSystemPrompt,
    messages: await convertWidgetMessagesToModelMessages(messages),
    stopWhen: stepCountIs(5),
  });
}
