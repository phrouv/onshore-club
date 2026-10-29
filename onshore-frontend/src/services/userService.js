import { get } from "../utils/api";

export const userService = {
  list: () => get("/api/users"),
};
