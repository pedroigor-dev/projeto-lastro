import { HttpInterceptorFn } from '@angular/common/http';

const DEVELOPMENT_KEY = 'local-development-key';

export const apiKeyInterceptor: HttpInterceptorFn = (request, next) => {
  const apiKey = localStorage.getItem('vertex-api-key') ?? DEVELOPMENT_KEY;
  return next(request.clone({ setHeaders: { 'X-API-Key': apiKey } }));
};
