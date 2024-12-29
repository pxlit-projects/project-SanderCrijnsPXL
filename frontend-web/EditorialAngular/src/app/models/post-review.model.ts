export class PostReview {
    id: number;
    title: string;
    content: string;
    author: string;
    dateCreated: Date;
    comment: String;

    constructor(id: number, title: string, content: string, author: string, dateCreated: Date, comment: String) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.dateCreated = dateCreated;
        this.comment = comment;
    }
}