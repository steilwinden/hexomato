import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: 'range',
  standalone: true
})
export class RangePipe implements PipeTransform {

  transform(max: number): number[] {
    return [...Array(max).keys()].map(i => i + 1);
  }
}
