export class Comment {
    id: number;
    postId: string;
    content: string;
    author: string;

    constructor(id: number, postId: string, content: string, author: string) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.author = author;
    }
}