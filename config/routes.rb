Rails.application.routes.draw do
  namespace :admin do
      resources :discogs
      resources :albums
      resources :tracks
      resources :musicians
      resources :performings
      resources :pairings
      resources :exports

      root to: "discogs#index"
    end
  # Define your application routes per the DSL in https://guides.rubyonrails.org/routing.html

  # Defines the root path route ("/")
  # root "articles#index"
  root 'home#index'
end
