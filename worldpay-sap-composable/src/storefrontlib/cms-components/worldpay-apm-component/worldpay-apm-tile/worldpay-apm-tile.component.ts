import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, HostListener, inject, Input } from '@angular/core';
import { MediaComponent } from '@spartacus/storefront';
import { Observable } from 'rxjs';
import { ApmData, WorldpayApmService } from '../../../../core';

@Component({
  selector: 'y-worldpay-apm-tile',
  templateUrl: './worldpay-apm-tile.component.html',
  styleUrls: ['./worldpay-apm-tile.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MediaComponent, AsyncPipe]
})
export class WorldpayApmTileComponent {
  @Input() apm: ApmData;
  protected worldpayApmService: WorldpayApmService = inject(WorldpayApmService);
  selectedApm$: Observable<ApmData> = this.worldpayApmService.getSelectedAPMFromState();

  @HostListener('click') selectApm(): void {
    this.worldpayApmService.selectAPM(this.apm);
  }
}
