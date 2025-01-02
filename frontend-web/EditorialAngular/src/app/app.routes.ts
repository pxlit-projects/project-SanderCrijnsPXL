import { Routes } from '@angular/router';
import { PostOverviewComponent } from './pages/post-overview/post-overview.component';
import { CreatePostComponent } from './pages/create-post/create-post.component';
import { PostsReviewOverviewComponent } from './pages/posts-review-overview/posts-review-overview.component';
import { AllPostsOverviewComponent } from './pages/all-posts-overview/all-posts-overview.component';
import { EditPostComponent } from './pages/edit-post/edit-post.component';
import { LoginComponent } from './pages/login/login.component';

export const routes: Routes = [
    { path: '', component:LoginComponent},
    { path: 'published-posts', component: PostOverviewComponent },
    { path: 'create', component: CreatePostComponent },
    { path: 'all-posts', component: AllPostsOverviewComponent },
    { path: 'review', component: PostsReviewOverviewComponent },
    { path: 'edit/:id', component: EditPostComponent },
];
