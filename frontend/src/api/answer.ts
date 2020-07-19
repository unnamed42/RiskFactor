import { request, IdType, IdResponse, ApiIdType } from "./base";

export interface AnswerInfo {
  id: IdType;
  creator: string;
  group: string;
  createdAt: number;
  modifiedAt: number;
}

export const getAnswer = (answerId: ApiIdType): Promise<any> =>
  request({ url: `/answers/${answerId}` });

export const answerModifiedAt = (answerId: ApiIdType): Promise<number> =>
  request({ url: `/answers/${answerId}/modifiedAt` });

/**
 * 获取现有的回答的列表
 */
export const getAnswerList = (schemaId: ApiIdType): Promise<AnswerInfo[]> =>
  request({ url: "/answers", params: { schemaId } });

export const createAnswer = (schemaId: ApiIdType, data: any): Promise<IdResponse> =>
  // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
  request({ url: "/answers", data, params: { schemaId }, method: "POST" });

export const updateAnswer = (answerId: ApiIdType, data: any): Promise<void> =>
  // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
  request({ url: `/answers/${answerId}`, data, method: "PUT" });

export const removeAnswer = (answerId: ApiIdType): Promise<void> =>
  request({ url: `/answers/${answerId}`, method: "DELETE" });
