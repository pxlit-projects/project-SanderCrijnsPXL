export class PostItem {
    title: string;
    content: string;
    author: string;
    dateCreated: Date;

    constructor(title: string, content: string, author: string, dateCreated: Date) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.dateCreated = dateCreated;
    }
}