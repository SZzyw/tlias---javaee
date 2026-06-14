import React from "react";
import { createRoot } from "react-dom/client";
import { OpenChatWidget } from "@openchatwidget/sdk";
import "@openchatwidget/sdk/styles.css";

const ROOT_ATTR = "data-openchatwidget-mounted";

class OpenChatWidgetElement extends HTMLElement {
  connectedCallback() {
    console.log("[OCW] connectedCallback fired", this);
    if (this.hasAttribute(ROOT_ATTR)) {
      console.log("[OCW] already mounted, skipping");
      return;
    }
    this.setAttribute(ROOT_ATTR, "");

    const url = this.getAttribute("url") || "/api/chat";
    const disableReasoning = this.hasAttribute("disable-reasoning");
    console.log("[OCW] mounting with url:", url);

    try {
      this._root = createRoot(this);
      console.log("[OCW] createRoot success");
      this._root.render(
        React.createElement(OpenChatWidget, {
          url,
          disableReasoning,
        })
      );
      console.log("[OCW] render called");
    } catch (e) {
      console.error("[OCW] mount error:", e);
    }
  }

  disconnectedCallback() {
    console.log("[OCW] disconnectedCallback");
    if (this._root) {
      this._root.unmount();
      this._root = null;
    }
  }

  static get observedAttributes() {
    return ["url", "disable-reasoning"];
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (!this._root || oldValue === newValue) return;

    const url = this.getAttribute("url") || "/api/chat";
    const disableReasoning = this.hasAttribute("disable-reasoning");

    this._root.render(
      React.createElement(OpenChatWidget, {
        url,
        disableReasoning,
      })
    );
  }
}

if (!customElements.get("open-chat-widget")) {
  customElements.define("open-chat-widget", OpenChatWidgetElement);
}
