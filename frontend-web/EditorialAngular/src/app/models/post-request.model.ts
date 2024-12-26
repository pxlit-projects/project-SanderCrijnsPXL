import { PostStatus } from "./post-status.model";

export interface PostRequest {
  title: string;
  content: string;
  author: string;
  status: PostStatus;
}