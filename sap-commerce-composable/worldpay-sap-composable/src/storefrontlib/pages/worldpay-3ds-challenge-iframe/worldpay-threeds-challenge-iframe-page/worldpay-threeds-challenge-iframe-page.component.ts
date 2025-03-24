import { AfterViewInit, Component, inject, OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { LoggerService, WindowRef } from '@spartacus/core';

@Component({
  templateUrl: './worldpay-threeds-challenge-iframe-page.component.html'
})
export class WorldpayThreedsChallengeIframePageComponent implements OnInit, AfterViewInit {
  action: SafeResourceUrl;
  md: string;
  jwt: string;
  protected logger: LoggerService = inject(LoggerService);
  private readonly ACTION_ROUTE_PARAM: string = 'action';
  private readonly MD_ROUTE_PARAM: string = 'md';
  private readonly JWT_ROUTE_PARAM: string = 'jwt';
  private readonly FORM_SELECTOR: string = '#challengeForm';

  constructor(
    private sanitizer: DomSanitizer,
    private route: ActivatedRoute,
    private winRef: WindowRef
  ) {
  }

  ngOnInit(): void {
    this.action = this.sanitizer.bypassSecurityTrustResourceUrl(
      this.route.snapshot.queryParams[this.ACTION_ROUTE_PARAM]
    );
    this.md = this.route.snapshot.queryParams[this.MD_ROUTE_PARAM];
    this.jwt = this.route.snapshot.queryParams[this.JWT_ROUTE_PARAM];
  }

  ngAfterViewInit(): void {
    if (!this.winRef.isBrowser()) {
      this.logger.log('SSR - skipping WorldpayThreedsChallengeIframePageComponent After View Init');
      return;
    }

    const form: HTMLFormElement = this.winRef.document.querySelector(this.FORM_SELECTOR);
    if (form) {
      form.submit();
    }
  }
}
