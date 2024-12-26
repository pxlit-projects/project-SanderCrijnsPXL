import { PostStatus } from "./post-status";

export interface PostRequest {
  title: string;
  content: string;
  author: string;
  status: PostStatus;
}