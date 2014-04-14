describe("Retail scan application", function () {

    var scope;

    beforeEach(module('retail-scan'));

    // Loads the controllers
    beforeEach(inject(function ($controller, $rootScope) {
        scope = $rootScope.$new();
        $controller('mainController', {$scope: scope});
    }));


    it("should have a success message initialized", inject(function () {
        expect(scope.message).toBeTruthy();
        expect(scope.message).toBe('Yeahhh ! You\'re ready !');
    }));

});