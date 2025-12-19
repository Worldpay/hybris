import { ChangeDetectionStrategy, Component, HostListener, Input } from '@angular/core';
import { Observable } from 'rxjs';
import { WorldpayApmService } from 'worldpay-sap-composable-services';
import { ApmData } from 'worldpay-sap-core';

@Component({
  selector: 'y-worldpay-apm-tile',
  templateUrl: './worldpay-apm-tile.component.html',
  styleUrls: ['./worldpay-apm-tile.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false
})
export class WorldpayApmTileComponent {
  @Input() apm: ApmData;
  selectedApm$: Observable<ApmData> = this.worldpayApmService.getSelectedAPMFromState();

  constructor(protected worldpayApmService: WorldpayApmService) { }

  @HostListener('click') selectApm(): void {
    this.worldpayApmService.selectAPM(this.apm);
  }
}
