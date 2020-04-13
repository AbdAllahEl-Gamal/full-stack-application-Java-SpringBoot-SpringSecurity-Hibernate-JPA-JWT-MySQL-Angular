// import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
// import {Observable} from "rxjs/internal/Observable";
// import {Injectable} from "@angular/core";

// @Injectable()
// export class Interceptor implements HttpInterceptor {

//   intercept(request: HttpRequest, next: HttpHandler): Observable> {
//     let token = window.localStorage.getItem('token');
//     if (token) {
//       request = request.clone({
//         setHeaders: {
//           Authorization: 'Bearer ' + token
//         }
//       });
//     }
//     return next.handle(request);
//   }
// }