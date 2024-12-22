export class Post {
    title: string;
    content: string;
    author: string;
    dateCreated: Date;
    isConcept: boolean;
    isPublished: boolean;

    constructor(title: string, content: string, author: string, dateCreated: Date, isConcept: boolean, isPublished: boolean) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.dateCreated = dateCreated;
        this.isConcept = isConcept;
        this.isPublished = isPublished;
    }
  }