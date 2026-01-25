import { Component, EventEmitter, inject, Output } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../shared/services/theme/theme.service';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss'],
    standalone: false
})
export class HeaderComponent {
    private router = inject(Router);
    private auth = inject(AuthService);
    private theme = inject(ThemeService);

    @Output() toggleSidebar = new EventEmitter<void>();

    isDark = false;

    constructor() {
        this.theme.current$.subscribe(theme => {
            this.isDark = theme === 'dark';
        });
    }

    onToggleTheme(): void {
        this.theme.toggle();
    }

    onLogout(): void {
        this.auth.logout();
        this.router.navigateByUrl('/auth/login', { replaceUrl: true });
    }
}
