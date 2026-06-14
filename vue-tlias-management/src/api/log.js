import request from "@/utils/request";

export const listApi = () => request.get("/log");
