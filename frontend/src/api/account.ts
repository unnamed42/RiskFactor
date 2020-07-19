import { request, IdType, ApiIdType } from "./base";

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

export const usernameExists = (username: string): Promise<void> =>
  request({ url: `/users/${username}`, method: "HEAD" });

/**
  * 获得指定用户的信息。特殊id 0代表自己
  */
export const userInfo = (userId: ApiIdType): Promise<UserInfo> =>
  request({ url: `/users/${userId}` });

/**
  * 获得当前用户所能管理的全部用户的信息
  */
export const userInfoList = (): Promise<UserInfo[]> =>
  request({ url: "/users" });

export const groupNames = (): Promise<string[]> =>
  request({ url: "/groups" });

export const updateUser = (targetId: ApiIdType, data: UpdateUserRequest): Promise<void> =>
  request({ url: `/users/${targetId}`, data, method: "PUT" });

export const createUser = (data: CreateUserRequest): Promise<void> =>
  request({ url: "/users", data, method: "POST" });

export const deleteUser = (targetId: ApiIdType): Promise<void> =>
  request({ url: `/users/${targetId}`, method: "DELETE" });
