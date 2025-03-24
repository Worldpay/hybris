import { AfterViewInit, Component, inject, OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { LoggerService, WindowRef } from '@spartacus/core';

@Component({
  templateUrl: './worldpay-ddc-iframe-page.component.html'
})
export class WorldpayDdcIframePageComponent implements OnInit, AfterViewInit {
  action: SafeResourceUrl;
  bin: string;
  jwt: string;
  protected logger: LoggerService = inject(LoggerService);
  private readonly ACTION_ROUTE_PARAM: string = 'action';
  private readonly BIN_ROUTE_PARAM: string = 'bin';
  private readonly JWT_ROUTE_PARAM: string = 'jwt';
  private readonly FORM_SELECTOR: string = '#collectionForm';

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
    this.bin = this.route.snapshot.queryParams[this.BIN_ROUTE_PARAM];
    this.jwt = this.route.snapshot.queryParams[this.JWT_ROUTE_PARAM];
  }

  ngAfterViewInit(): void {
    if (!this.winRef.isBrowser()) {
      this.logger.log('SSR - skipping WorldpayDdcIframePageComponent After View Init');
      return;
    }

    const formElement: HTMLFormElement = this.winRef.document.querySelector(this.FORM_SELECTOR);
    if (formElement) {
      formElement.submit();
    }
  }
}
