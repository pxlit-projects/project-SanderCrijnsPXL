import { Routes } from '@angular/router';
import { PostOverviewComponent } from './pages/post-overview/post-overview.component';
import { CreatePostComponent } from './pages/create-post/create-post.component';

export const routes: Routes = [
    { path: '', component: PostOverviewComponent },
    { path: 'create', component: CreatePostComponent },
];
