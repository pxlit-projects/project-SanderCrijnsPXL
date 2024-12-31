import { Comment } from "./comment.model";

export class PublishedPost {
    id: number;
    title: string;
    content: string;
    author: string;
    dateCreated: Date;
    comments: Comment[] = [];

    constructor(id: number, title: string, content: string, author: string, dateCreated: Date, comments: Comment[]) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.dateCreated = dateCreated;
        this.comments = comments;
    }
}