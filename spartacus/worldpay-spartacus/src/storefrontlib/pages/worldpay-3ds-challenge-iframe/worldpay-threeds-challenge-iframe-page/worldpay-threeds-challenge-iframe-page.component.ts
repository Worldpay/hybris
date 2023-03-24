import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { WindowRef } from '@spartacus/core';

@Component({
  templateUrl: './worldpay-threeds-challenge-iframe-page.component.html'
})
export class WorldpayThreedsChallengeIframePageComponent
implements OnInit, AfterViewInit {
  private readonly ACTION_ROUTE_PARAM: string = 'action';
  private readonly MD_ROUTE_PARAM: string = 'md';
  private readonly JWT_ROUTE_PARAM: string = 'jwt';
  private readonly FORM_SELECTOR: string = '#challengeForm';

  action: SafeResourceUrl;
  md: string;
  jwt: string;

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
    const form: HTMLFormElement = this.winRef.document.querySelector(this.FORM_SELECTOR);
    if (form) {
      form.submit();
    }
  }
}
