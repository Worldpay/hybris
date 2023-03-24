/**
 *
 * @param src script url to load
 * @param onloadCallback callback function is called when new script is loaded
 */
import { WindowRef } from '@spartacus/core';
import { Injectable } from '@angular/core';

export interface Scripts {
  idScript: string;
  src: string;
  onloadCallback?: any;
  async?: boolean;
  defer?: boolean;
  attributes?: {
    [key: string]: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class LoadScriptService {

  window = this.winRef.nativeWindow as any;

  constructor(
    protected winRef: WindowRef
  ) {
  }

  loadScript(
    {
      idScript,
      src,
      onloadCallback,
      async,
      defer,
      attributes,
    }: Scripts
  ): void {
    let isFound = false;
    const scripts = this.winRef.document.getElementsByTagName('script');
    for (let i = 0; i < scripts.length; ++i) {
      if (
        scripts[i].getAttribute('src') != null &&
        scripts[i].getAttribute('src') === src
      ) {
        isFound = true;
        break;
      }
    }

    if (!isFound) {
      let node = this.winRef.document.createElement('script');
      node.src = src;
      node.id = idScript || Math.floor(Math.random() * 999999).toString();
      node.type = 'text/javascript';
      if (async) {
        node.async = async;
      }
      if (defer) {
        node.defer = defer;
      }
      if (onloadCallback) {
        node.onload = onloadCallback;
      }

      if (attributes) {
        node = this.updateScript(node, attributes);
      }
      this.winRef.document.getElementsByTagName('head')[0].appendChild(node);
    }

  };

  removeScript(idScript: string): void {
    const script = this.winRef.document.querySelector(`script#${idScript}`);
    if (script) {
      script.remove();
    }
  }

  updateScript(node, attributes): any {
    if (node && attributes) {
      for (const key of Object.keys(attributes)) {
        node.setAttribute(key, attributes[key]);
      }
    }

    return node;
  }
}
