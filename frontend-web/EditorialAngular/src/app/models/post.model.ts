import { PostStatus } from "./post-status.model";

export class Post {
    id: number;
    title: string;
    content: string;
    author: string;
    dateCreated: Date;
    status: String;

    constructor(id: number, title: string, content: string, author: string, dateCreated: Date, status: PostStatus) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.dateCreated = dateCreated;
        this.status = status;
    }
}