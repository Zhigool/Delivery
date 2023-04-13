package ru.sapronov.api_testing


import org.springframework.http.HttpStatus
import ru.sapronov.common.dto.PointDto
import ru.sapronov.order.dto.ChangeOrderDestinationRequestDto
import ru.sapronov.order.dto.CreateOrderRequestDto
import ru.sapronov.tracking.dto.SaveTrackingInfoRequestDto

class OrderApiTest extends BaseApiTest {

    def "Create order by client"() {
        given:
        def courierAccessToken = loginCourier(true)
        trackingService.saveTrackingInfo(new SaveTrackingInfoRequestDto(
                0.0,
                0.0
        ), courierAccessToken)
        def request = new CreateOrderRequestDto(
                new PointDto(0.1, 0.1),
                new PointDto(1.0, 2.0)
        )
        def clientAccessToken = loginClient()

        when:
        def response = orderService.createOrder(request, clientAccessToken)

        then:
        response.statusCode == HttpStatus.OK
    }

    def "Change order destination by client"() {
        given:
        def courierAccessToken = loginCourier(true)
        trackingService.saveTrackingInfo(new SaveTrackingInfoRequestDto(
                0.0,
                0.0
        ), courierAccessToken)
        def request = new CreateOrderRequestDto(
                new PointDto(0.1, 0.1),
                new PointDto(1.0, 2.0)
        )
        def clientAccessToken = loginClient()
        createOrder(request, clientAccessToken)
        def orderId = getOrderIdByClientEmail(test1email)
        def newDestinationPoint = new PointDto(0.3, 0.9)

        when:
        def response = orderService.changeOrderDestination(new ChangeOrderDestinationRequestDto(
                orderId,
                newDestinationPoint
        ), clientAccessToken)

        then:
        waitBefore()
        response.statusCode == HttpStatus.OK
    }

    def "Client cancel the order"() {
        given:
        def courierAccessToken = loginCourier(true)
        trackingService.saveTrackingInfo(new SaveTrackingInfoRequestDto(
                0.0,
                0.0
        ), courierAccessToken)
        def request = new CreateOrderRequestDto(
                new PointDto(0.1, 0.1),
                new PointDto(1.0, 2.0)
        )
        def clientAccessToken = loginClient()
        createOrder(request, clientAccessToken)
        def orderId = getOrderIdByClientEmail(test1email)

        when:
        def response = orderService.cancelOrder(orderId, clientAccessToken)

        then:
        response.statusCode == HttpStatus.OK
    }

    def "Client can see the details of a delivery"() { // delivery is combination of order, courier info and tracking
        given:
        def courierAccessToken = loginCourier(false)
        def adminAccessToken = loginAdmin()
        authService.appointCourier(test2email, adminAccessToken)
        trackingService.saveTrackingInfo(new SaveTrackingInfoRequestDto(
                0.0,
                0.0
        ), courierAccessToken)
        def request = new CreateOrderRequestDto(
                new PointDto(0.1, 0.1),
                new PointDto(1.0, 2.0)
        )
        def clientAccessToken = loginClient()
        createOrder(request, clientAccessToken)
        def orderId = getOrderIdByClientEmail(test1email)

        when:
        def orderResponse = orderService.getOrderDetails(orderId, clientAccessToken)
        def trackingResponse = trackingService.getTrackingInfo(
                orderId,
                clientAccessToken)

        then:
        waitBefore()
        orderResponse.statusCode == HttpStatus.OK
                && trackingResponse.statusCode == HttpStatus.OK
                && trackingResponse.body.latitude() != null
                && trackingResponse.body.longitude() != null
    }

    def "Client can see all parcel delivery orders that he/she created"() {
        given:
        def courierAccessToken = loginCourier(false)
        def adminAccessToken = loginAdmin()
        authService.appointCourier(test2email, adminAccessToken)
        trackingService.saveTrackingInfo(new SaveTrackingInfoRequestDto(
                0.0,
                0.0
        ), courierAccessToken)
        def requestOrder1 = new CreateOrderRequestDto(
                new PointDto(0.1, 0.1),
                new PointDto(1.0, 2.0)
        )
        def requestOrder2 = new CreateOrderRequestDto(
                new PointDto(0.2, 0.1),
                new PointDto(1.0, 2.7)
        )
        def clientAccessToken = loginClient()
        createOrder(requestOrder1, clientAccessToken)
        createOrder(requestOrder2, clientAccessToken)

        when:
        def response = orderService.getOrders(clientAccessToken)

        then:
        response.statusCode == HttpStatus.OK && response.body.size() == 2
    }

    def "Admin can change the status of a parcel delivery order"() {
        given:
        def courierAccessToken = loginCourier(true)
        def adminAccessToken = loginAdmin()
        authService.appointCourier(test2email, adminAccessToken)
        trackingService.saveTrackingInfo(new SaveTrackingInfoRequestDto(
                0.0,
                0.0
        ), courierAccessToken)
        def request = new CreateOrderRequestDto(
                new PointDto(0.1, 0.1),
                new PointDto(1.0, 2.0)
        )
        def clientAccessToken = loginClient()
        createOrder(request, clientAccessToken)
        def orderId = getOrderIdByClientEmail(test1email)

        when:
        def response = orderService.cancelOrder(orderId, adminAccessToken)

        then:
        response.statusCode == HttpStatus.OK
    }

    def "Admin can view all parcel delivery orders"() {
        given:
        def courierAccessToken = loginCourier(false)
        def adminAccessToken = loginAdmin()
        authService.appointCourier(test2email, adminAccessToken)
        trackingService.saveTrackingInfo(new SaveTrackingInfoRequestDto(
                0.0,
                0.0
        ), courierAccessToken)
        def requestOrder1 = new CreateOrderRequestDto(
                new PointDto(0.1, 0.1),
                new PointDto(1.0, 2.0)
        )
        def requestOrder2 = new CreateOrderRequestDto(
                new PointDto(0.2, 0.1),
                new PointDto(1.0, 2.7)
        )
        def clientAccessToken = loginClient()
        createOrder(requestOrder1, clientAccessToken)
        createOrder(requestOrder2, clientAccessToken)

        when:
        def response = orderService.getOrders(adminAccessToken)

        then:
        response.statusCode == HttpStatus.OK && response.body.size() == 2
    }

    def "Admin can assign parcel delivery order to courier"() {
        given:
        def courierAccessToken = loginCourier(true)
        def adminAccessToken = loginAdmin()
        authService.appointCourier(test2email, adminAccessToken)
        trackingService.saveTrackingInfo(new SaveTrackingInfoRequestDto(
                0.0,
                0.0
        ), courierAccessToken)
        def request = new CreateOrderRequestDto(
                new PointDto(0.1, 0.1),
                new PointDto(1.0, 2.0)
        )
        def clientAccessToken = loginClient()
        createOrder(request, clientAccessToken)
        def orderId = getOrderIdByClientEmail(test1email)
        def courierId = getUserIdByEmail(test2email)
        changeOrderStatusBySql(orderId, "CREATED") //делаем вид что евент от tracking-service не был обработак и требуется вмешательство админа

        when:
        def response = orderService.assignCourierToOrder(orderId, courierId, adminAccessToken)

        then:
        response.statusCode == HttpStatus.OK && getOrderCourierId(orderId) == courierId
    }

    def "Admin can track the delivery order by coordinates"() {
        given:
        def courierAccessToken = loginCourier(false)
        def adminAccessToken = loginAdmin()
        authService.appointCourier(test2email, adminAccessToken)
        trackingService.saveTrackingInfo(new SaveTrackingInfoRequestDto(
                0.0,
                0.0
        ), courierAccessToken)
        def request = new CreateOrderRequestDto(
                new PointDto(0.1, 0.1),
                new PointDto(1.0, 2.0)
        )
        def clientAccessToken = loginClient()
        createOrder(request, clientAccessToken)
        def orderId = getOrderIdByClientEmail(test1email)

        when:
        def orderResponse = orderService.getOrderDetails(orderId, adminAccessToken)
        def trackingResponse = trackingService.getTrackingInfo(
                orderId,
                adminAccessToken)

        then:
        waitBefore()
        orderResponse.statusCode == HttpStatus.OK
                && trackingResponse.statusCode == HttpStatus.OK
                && trackingResponse.body.latitude() != null
                && trackingResponse.body.longitude() != null
    }

    def "Courier can view all parcel delivery orders that assigned to him"() {
        given:
        loginCourier(false)
        def adminAccessToken = loginAdmin()
        authService.appointCourier(test2email, adminAccessToken)
        def courierAccessToken = getTest2AccessToken()
        trackingService.saveTrackingInfo(new SaveTrackingInfoRequestDto(
                0.0,
                0.0
        ), courierAccessToken)
        def requestOrder1 = new CreateOrderRequestDto(
                new PointDto(0.1, 0.1),
                new PointDto(1.0, 2.0)
        )
        def requestOrder2 = new CreateOrderRequestDto(
                new PointDto(0.1, 2.1),
                new PointDto(1.5, 2.0)
        )
        def clientAccessToken = loginClient()
        createOrder(requestOrder1, clientAccessToken)
        createOrder(requestOrder2, clientAccessToken)

        when:
        waitBefore()
        def response = orderService.getOrders(courierAccessToken)

        then:
        response.statusCode == HttpStatus.OK && response.body.size() == 2
    }

    def "Courier can change the status of a parcel delivery order"() {
        given:
        loginCourier(false)
        def adminAccessToken = loginAdmin()
        authService.appointCourier(test2email, adminAccessToken)
        def courierAccessToken = getTest2AccessToken()
        trackingService.saveTrackingInfo(new SaveTrackingInfoRequestDto(
                0.0,
                0.0
        ), courierAccessToken)
        def requestOrder = new CreateOrderRequestDto(
                new PointDto(0.1, 0.1),
                new PointDto(1.0, 2.0)
        )
        def clientAccessToken = loginClient()
        createOrder(requestOrder, clientAccessToken)
        def orderId = getOrderIdByClientEmail(test1email)

        when:
        waitBefore()
        def response = orderService.finishOrder(orderId, courierAccessToken)

        then:
        response.statusCode == HttpStatus.OK && getOrderStatusFromDb(orderId) == "DELIVERED"
    }
}
