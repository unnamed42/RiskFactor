import { request, downloadAsFile } from "./http";

import {
  IdResponse,
  TaskView, TaskStruct, AnswerBrief, Question
} from "@/types";

/**
 * 获取Task的基础信息
 * @param taskId Task的id
 */
export const task = (taskId: number | string) =>
  request<TaskView>({ url: `/tasks/${taskId}` });

/**
 * 获取当前用户能访问的所有Task
 */
export const tasks = () =>
  request<TaskView[]>({ url: "/tasks" });

/**
 * 获取Task下属的所有分节的详细信息
 * @param taskId Task的id
 */
export const taskLayout = (taskId: number | string) =>
  request<Question[]>({ url: `/tasks/${taskId}/layout` });

export const taskMtime = (taskId: number | string) =>
  request<{ mtime: number }>({ url: `/tasks/${taskId}/mtime` });

/**
 * 获取Task下属的所有分节的基础信息
 * @param taskId Task的id
 */
export const taskStructure = (taskId: number | string) =>
  request<TaskStruct[]>({ url: `/tasks/${taskId}/sections/name` });

/**
 * 获取提交给Task的所有问卷回复的基础信息
 * @param taskId Task的id
 */
export const taskAnswers = (taskId: number | string) =>
  request<AnswerBrief[]>({ url: `/tasks/${taskId}/answers` });

export const answer = (answerId: number | string) =>
  request<any>({ url: `/answers/${answerId}/body` });

export const deleteAnswer = (answerId: number | string) =>
  request<void>({ url: `/answers/${answerId}`, method: "DELETE" });

export const downloadAnswer = (answerId: number | string) =>
  request<Blob>({ url: `/answers/${answerId}/file`, responseType: "blob" })
    .then(data => downloadAsFile(data, "export.json"));

export const postAnswer = (taskId: number | string, body: any) =>
  request<IdResponse>({ url: `/tasks/${taskId}/answers`, data: body, method: "POST" });

export const updateAnswer = (taskId: number | string, answerId: number | string, patches: any) =>
  request<IdResponse>({ url: `/tasks/${taskId}/answers/${answerId}`, data: patches, method: "PUT" });
