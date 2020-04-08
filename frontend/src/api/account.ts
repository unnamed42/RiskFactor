import { request, IdType } from "./base";

export interface UserInfo {
  id: IdType;
  username?: string;
  email?: string;
  group?: string;
  isAdmin?: boolean;
}

export interface UpdateUserRequest extends Omit<UserInfo, "id"> {
  password?: string;
  group?: string;
}

export interface CreateUserRequest extends Omit<UpdateUserRequest, "password"> {
  password: string;
}

export const usernameExists = (username: string) =>
  request({ url: `/users/${username}`, method: "HEAD" });

/**
  * 获得指定用户的信息。特殊id 0代表自己
  */
export const userInfo = (userId: IdType) =>
  request<UserInfo>({ url: `/users/${userId}` });

/**
  * 获得当前用户所能管理的全部用户的信息
  */
export const userInfoList = () =>
  request<UserInfo[]>({ url: "/users" });

export const updateUser = (targetId: IdType, data: UpdateUserRequest) =>
  request({ url: `/users/${targetId}`, data, method: "PUT" });

export const createUser = (data: CreateUserRequest) =>
  request({ url: "/users", data, method: "POST" });

export const deleteUser = (targetId: IdType) =>
  request({ url: `/users/${targetId}`, method: "DELETE" });
